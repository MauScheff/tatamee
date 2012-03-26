from google.appengine.ext import db
from google.appengine.ext import webapp
from datamodel import Item
import wsgiref.handlers
import logging
import datetime
import delete

class MainHandler(webapp.RequestHandler):
  def get(self):
    #TODO: Change loop to redirect
    oldest_possible_date = datetime.datetime.now() - datetime.timedelta(days=30)  
    items = Item.all().filter('timestamp <', oldest_possible_date).fetch(1000)
    size = len(items)
    for item in items:
        delete.delete_item(item)
    if (size >= 1000):
        self.redirect('/delete/delete_expired_items', true)
        return
    self.response.out.write('All expired items have been deleted')
    
def main():
  application = webapp.WSGIApplication([('/delete/delete_expired_items', MainHandler)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()