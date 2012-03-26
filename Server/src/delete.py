from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext import webapp
from datamodel import Item
from datamodel import ItemLight
from datamodel import Picture
from datamodel import User
from datamodel import Thumbnail
import wsgiref.handlers
import simplejson
import logging

class MainHandler(webapp.RequestHandler):
  def post(self):
    id = self.request.get("id")
    #TODO: handle key not found
    #TODO: Run as transaction
    item_key = db.Key(id)
    item = db.get(item_key)
    item_title = item.title
    user = users.get_current_user()
    
    if (db.get(item_key.parent()).user == user):
        delete_item(item)
    
    logging.info(user.nickname() + ' deleted item ' + item_title)
    self.response.out.write('Success')

def delete_item(item):
    item_title = item.title
    item_keys_to_delete = [item.key()]
    item_keys_to_delete += [picture.key() for picture in Picture.all().ancestor(item).fetch(1000)]
    item_keys_to_delete += [thumbnail.key() for thumbnail in Thumbnail.all().ancestor(item).fetch(1000)]
    item_keys_to_delete += [itemlight.key() for itemlight in item.itemlight_set]
    db.delete(item_keys_to_delete)
    
def main():
  application = webapp.WSGIApplication([('/delete', MainHandler)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()
