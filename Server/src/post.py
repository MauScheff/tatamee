from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext import webapp
from datamodel import Item
from datamodel import User
from datamodel import Picture
from datamodel import MyEncoder
from datamodel import ItemLight
import wsgiref.handlers
import simplejson
import datetime
import logging
import operator
import cgi
from google.appengine.api import images
from google.appengine.ext.db import Property
import image as image_post
from itertools import chain
            
#TODO: Items must be written in transaction mode            
class MainHandler(webapp.RequestHandler):
  def post(self):
    id = self.request.get("id")
    if id != '':
      item_key = db.Key(id)
      item = db.get(item_key)
      item_parts_to_write = []
      user_id = users.get_current_user().user_id()
      user = User.get_by_key_name('key:' + user_id)
      if (item.parent().user == users.get_current_user()):
        item.title = self.request.get("title")
        item.price = float(self.request.get("price"))
        item.description = self.request.get("description")
        item.category = self.request.get("category")
        item.tags = self.request.get("tags").lower()
        item.location = user.home
        item.currency = user.currency
        
        item.timestamp = datetime.datetime.now()
        item_parts_to_write.append(item)
        
        category_string = pluralize(clean_word(self.request.get("category").lower()))
        
        if self.request.get("img"):
          image = images.Image(self.request.get("img"))
          image_post.write_image(item, image, 0, "")
        
        tags = remove_duplicates([clean_word(word) for word in self.request.get("tags").split(',')])
        extra_tags = ['']  
        for tag in tags:
            if len(tag.split()) > 1:
                tags.extend(tag.split())
                extra_tags.extend(tag.split())
        if category_string:
            tags.append(category_string)
            extra_tags.append(category_string)
        singulars = get_singulars(tags)
        tags += singulars   
        extra_tags += singulars    
        title_words = [clean_word(word) for word in item.title.split(' ')] 
        tags += title_words
        extra_tags += title_words
         
        # Delete old item light so that when sorted by __key__ ascending (default)
        # Oldest items (about to expire) show first.
        #TODO check if touching or re putting is enough?
        db.delete(item.itemlight_set[0])
        item_light = ItemLight(parent=user, title=item.title, item_key=item.key(), location=user.home, area=user.area, tags=tags, extra_tags=extra_tags)
        item_parts_to_write.append(item_light)
        
        db.put(item_parts_to_write)  
        logging.info('Item was updated: %s, by user: %s' % (item.title, item.parent().user.nickname()))
        
    else:
      user_id = users.get_current_user().user_id()
      user = User.get_by_key_name('key:' + user_id)
      item = Item(parent=user, location=user.home)
      item.currency = user.currency
      item.title = self.request.get("title")
      item.category = self.request.get("category")
      item.tags = self.request.get("tags").lower()
      item.price = float(self.request.get("price"))
      item.description = self.request.get("description")
      item_key = item.put()
      
      category_string = pluralize(clean_word(self.request.get("category").lower()))
      
      if self.request.get("img"):
          image = images.Image(self.request.get("img"))
          image_post.write_image(item, image, 0, "")
      
      tags = remove_duplicates([clean_word(word) for word in self.request.get("tags").split(',')])
      extra_tags = ['']  
      for tag in tags:
          if len(tag.split()) > 1:
              tags.extend(tag.split())
              extra_tags.extend(tag.split())
      if category_string:
          tags.append(category_string)
          extra_tags.append(category_string)
      singulars = get_singulars(tags)
      tags += singulars   
      extra_tags += singulars    
      title_words = [clean_word(word) for word in item.title.split(' ')] 
      tags += title_words
      extra_tags += title_words
        
      item_light = ItemLight(parent=user, title=item.title, item_key=item_key, location=user.home, area=user.area, tags=tags, extra_tags=extra_tags)
      item_light.put()
      
      logging.info('Item was created: %s, by user: %s' % (item.title, item.parent().user.nickname()))
      
    self.response.out.write(cgi.escape(MyEncoder.encode(MyEncoder(), item)))
    self.response.headers.add_header('Location', '/item/' + str(item.key()))
    self.response.set_status(201)
    
  def get(self):
      self.redirect("/home")
      
def main():
  application = webapp.WSGIApplication([('/post', MainHandler)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)
  
def clean_word(word):
  return word.strip().lower()
  
def pluralize(word):  
  if not word.endswith('s'):
      word += 's'
  return word    
  
def remove_duplicates(words):
  return list(set(words))
  
def get_singulars(words):
  result = []  
  for word in words:
      if word.endswith('s'):
          if word[:-1] != '':
            result.append(word[:-1])
          if word[:-2] != '':    
            result.append(word[:-2])
  return result         

if __name__ == '__main__':
  main()

#>>> import operator
#>>> L = [('c', 2), ('d', 1), ('a', 4), ('b', 3)]
#>>> sorted(L, key=operator.itemgetter(1))
#[('d', 1), ('c', 2), ('b', 3), ('a', 4)]
