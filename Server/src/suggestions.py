from google.appengine.ext import db
from google.appengine.ext import webapp
from google.appengine.api import users
from datamodel import User
from datamodel import ItemLight
import simplejson
import wsgiref.handlers
import geo
import cgi
import string
import logging
import heapq
from google.appengine.api import memcache
from operator import itemgetter

class MainHandler(webapp.RequestHandler):
  def get(self):    
      
      suggestions = {}     
      areas = set()
      query_string = self.request.get("query")
      location = self.request.get("location")
      
      if location == 'me':
          user_id = users.get_current_user().user_id()
          user = User.get_by_key_name('key:' + user_id)
          areas.add(str(user.area))
          
      elif location == 'map':
          min_lat = float(self.request.get('min_lat'))
          min_lng = float(self.request.get('min_lng'))
          max_lat = float(self.request.get('max_lat'))
          max_lng = float(self.request.get('max_lng'))
          areas = geo.getInnerSquares(db.GeoPt(min_lat, min_lng), db.GeoPt(max_lat, max_lng))
      
      #TODO: Optimization could reduce number of calls 
      if (self.request.get("action") == "tags"):
          suggestions = get_suggestions(query_string, areas)
      #    while len(areas) > 0:
      #        area = areas.pop()
      #        category = Category.all().filter('name =', category_string).filter('area =', area).get()
      #        if category:
      #            tags = category.tag_set.fetch(1000)
      #            for tag in tags:
      #                suggestions[tag.value] = suggestions.get(tag.value, 0) + 1
      #    suggestions = heapq.nlargest(100, suggestions.iteritems(), itemgetter(1))
                 
      self.response.out.write('{Suggestions: ' + simplejson.dumps(suggestions) + "}")

def get_suggestions(query_strings, areas):
    suggestions = {}
    #TODO Sort strings so key is same
    query_strings = [word.strip().lower() for word in query_strings.split()]
    query_string = ','.join(sorted(query_strings))   
    while len(areas) > 0:
        area = areas.pop()
        cache_key = ''.join(['suggestion|', area, '|', query_string])
        result = memcache.get(cache_key)
        if result is not None:
          result = dict( result )
          if suggestions:  
              suggestions = dict( (key, suggestions.get(key, 0) + result.get(key, 0)) for key in set(suggestions)|set(result) )
          else:
              suggestions = result    
        else:
          query = db.Query(ItemLight)
          query.filter("area =", area)
          for term in query_strings:
              query.filter("tags =", term)
          items = query.fetch(100)
          result = {}
          for item in items:
              for tag in (set(item.tags) - set(item.extra_tags)):
                  result[tag] = result.get(tag, 0) + 1
                  suggestions[tag] = suggestions.get(tag, 0) + 1
          #TODO CHANGE 1 Second to more
          memcache.add(cache_key, result, 1)
            
    return heapq.nlargest(100, suggestions.iteritems(), itemgetter(1))    
      
def pluralize(word):  
    if not word.endswith('s'):
        word += 's'
    return word      

def clean_word(word):
  return word.strip().lower()    
      
def main():
  application = webapp.WSGIApplication([('/suggestions', MainHandler)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()