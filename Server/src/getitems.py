from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext import webapp
from datamodel import Item
from datamodel import ItemLight
from datamodel import User
from datamodel import MyEncoder
from datamodel import Comment
from datamodel import GoodRating
from datamodel import BadRating
import string
import simplejson
import logging
import wsgiref.handlers
import geo
import cgi

class MainHandler(webapp.RequestHandler):
  def get(self):      
    if (self.request.get("action") == "map"):
      #TODO: Add results from categories with high edges 
      min_lat = float(self.request.get('min_lat'))
      min_lng = float(self.request.get('min_lng'))
      max_lat = float(self.request.get('max_lat'))
      max_lng = float(self.request.get('max_lng'))
      search_terms = [word.strip().lower() for word in self.request.get('search_term').split()]
      result = []
      tags = {}
      areas = geo.getInnerSquares(db.GeoPt(min_lat, min_lng), db.GeoPt(max_lat, max_lng))
      while len(areas) > 0:
          query = db.Query(ItemLight)
          query.filter("area =", areas.pop())
          for term in search_terms:
              query.filter("tags =", term)
          items = query.fetch(200)
          for item in items:
              result.append(item)
      
      self.response.out.write(cgi.escape('{Items: ' + MyEncoder.encode(MyEncoder(), result) + "}"))
      
    elif (self.request.get("action") == "user"):
      user_id = self.request.get("user_id")
      offset = int(self.request.get("offset"))
      user = User.get_by_key_name('key:' + user_id)
      items = []
      if user:
          items = ItemLight.all().ancestor(user).fetch(1000, offset=offset)
      self.response.out.write(cgi.escape('{Items: ' + MyEncoder.encode(MyEncoder(), items) + "}"))
      return
  
    elif (self.request.get("action") == "me"):
      items = []
      user = users.get_current_user()
      if user:
          user = User.get_by_key_name('key:' + user.user_id())
          query = db.Query(ItemLight)
          query.ancestor(user)
          items = query.fetch(1000)
      self.response.out.write(cgi.escape('{Items: ' + MyEncoder.encode(MyEncoder(), items) + "}"))
      
    elif (self.request.get("action") == "item"):
      key = self.request.get("key")
      item = Item.get(key)
      self.response.out.write(MyEncoder.encode(MyEncoder(), item))
      
    elif (self.request.get("action") == "comments"):
      offset = int(self.request.get("offset"))  
      key = self.request.get("id")
      item = Item.get(key)
      comments = Comment.all().ancestor(item).order('-__key__').fetch(5, offset=offset)
      self.response.out.write(cgi.escape('{Messages: ' + MyEncoder.encode(MyEncoder(), comments) + '}'))
      
    elif (self.request.get("action") == "good_ratings"):
      offset = int(self.request.get("offset"))  
      key = self.request.get("id")
      item = Item.get(key)
      ratings = GoodRating.all().ancestor(item.parent()).order('-__key__').fetch(5, offset=offset)
      logging.info(MyEncoder.encode(MyEncoder(), ratings))
      self.response.out.write(cgi.escape('{Messages: ' + MyEncoder.encode(MyEncoder(), ratings) + '}'))
      
    elif (self.request.get("action") == "bad_ratings"):
      offset = int(self.request.get("offset"))  
      key = self.request.get("id")
      item = Item.get(key)
      ratings = BadRating.all().ancestor(item.parent()).order('-__key__').fetch(5, offset=offset)
      self.response.out.write(cgi.escape('{Messages: ' + MyEncoder.encode(MyEncoder(), ratings) + '}'))    
      
def main():
  application = webapp.WSGIApplication([('/getitems', MainHandler)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()
