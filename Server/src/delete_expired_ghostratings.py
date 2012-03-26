from google.appengine.ext import db
from google.appengine.ext import webapp
from datamodel import Item
from datamodel import GhostRating
import wsgiref.handlers
import logging
import datetime
import delete

class MainHandler(webapp.RequestHandler):
  def get(self):
    #TODO: Change loop to redirect
    oldest_possible_date = datetime.datetime.now() - datetime.timedelta(days=30)  
    ratings = GhostRating.all().filter('timestamp <', oldest_possible_date).fetch(1000)
    size = len(ratings)
    db.delete(ratings)
    if (size >= 1000):
        self.redirect('/delete/delete_expired_ghostratings', true)
        return
    self.response.out.write('All expired ghost ratings have been deleted')
    
def main():
  application = webapp.WSGIApplication([('/delete/delete_expired_ghostratings', MainHandler)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()