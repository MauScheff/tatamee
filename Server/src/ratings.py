from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
import os
from datamodel import Item
from datamodel import User
from datamodel import Comment
from datamodel import GhostRating
from datamodel import GoodRating
from datamodel import BadRating
from google.appengine.api import mail
import wsgiref.handlers
import simplejson
import logging
import hashlib

class MainHandler(webapp.RequestHandler):
  def post(self):
    email = self.request.get("email")
    id = self.request.get("id")
    
    if not mail.is_email_valid(email):
        invalid_reason = mail.invalid_email_reason(email, email)
        self.response.out.write("Invalid Email")
        return
    
    item_key = db.Key(id)
    item = db.get(item_key)
    user = item.parent()
    
    if (user.user == users.get_current_user() and user.user.email() != email):
       ghost_rating = GhostRating(parent=user)
       ghost_rating.put()
       id = hashlib.sha1(str(ghost_rating.key())).hexdigest()
       ghost_rating.id = id
       ghost_rating.email = db.Email(email)
       ghost_rating.put()
       
       url = 'http://www.tatamee.com/ratings?id=' + id
       email_message = '''
Hello %s       
       
%s would like you to rate your recent transaction.
You may give a good or bad rating based on your experience.

If you would like to give a rating to %s please go to the following url:
%s

Regards,
The Tatamee Team
      
*** IMPORTANT ***
Please do not reply back to this message.
Instead, you may write an email to %s
''' % (email[:email.find('@')], user.user.nickname(), user.user.nickname(), url, user.user.email()) 
      
    sender = 'mail@tatamee.com'
    mail.send_mail(sender, email, 'Tatamee Rating Request [%s]' % item.title, email_message)    
    logging.info('Rating request sent to: %s, by: %s' % (email, user.user.email()))
    self.response.out.write("Success")
    self.response.set_status(201)
    
  def get(self):
    id = self.request.get("id")
    ghostrating = GhostRating.all().filter('id', id).get()
    if not ghostrating:
        id = ''
        user = ''
    else:
        user = ghostrating.parent().user.nickname()    
    template_values = {'locale': MainHandler.getLocale(self), 'id': id, 'user': user}  
    path = os.path.join(os.path.dirname(__file__), 'Ratings.html')
    self.response.out.write(template.render(path , template_values))
  
  def getLocale(self):
      default = 'en'
      accepted = ('en', 'es', 'fr', 'sq', 'bg', 'ca', 'zh', 'hr', 'cs', 'da', 'nl', 'et', 'fi',
                  'gl', 'de', 'el', 'hi', 'hu', 'id', 'it', 'ja', 'ko', 'lv', 'lt', 'no', 'pl'
                  'pt', 'ro', 'ru', 'sr', 'sk', 'sl', 'sv', 'th', 'tr', 'uk', 'vi')
      pref_langs = self.request.headers['Accept-Language'].split(',')
      for lan in pref_langs:
          if lan.strip()[:2] in accepted:
              default = lan.strip()[:2]
              break
      return default

class PostHandler(webapp.RequestHandler):
    def post(self):
        id = self.request.get("id")
        message = self.request.get("message")
        rating = self.request.get("rating")
        ghostrating = GhostRating.all().filter('id', id).get()
        if ghostrating:
            user = ghostrating.parent()
            if (rating == 'good'):
                rating = GoodRating(parent=user)
                user.good_ratings += 1
            elif (rating == 'bad'):
                rating = BadRating(parent=user)
                user.bad_ratings += 1
            user.put()    
            rating.message = message
            rating.email = ghostrating.email
            rating.put()
            ghostrating.delete()
            self.response.out.write("Success")
            self.response.set_status(201)
            return
        self.response.set_status(400)
        return

class ReportHandler(webapp.RequestHandler):
    def post(self):
        id = self.request.get("id")
        message = self.request.get("message")
        report = int(self.request.get("report"))
        item_key = db.Key(id)
        item = db.get(item_key)
        user = item.parent()
        rating = BadRating(parent=user)
        rating.report = report
        rating.message = 'Tatamee report message: \n' + message
        rating.email = db.Email('anonymous@tatamee.com')
        rating.put()
        user.bad_ratings += 1
        user.put()    
            
        self.response.out.write("Success")
        self.response.set_status(201)
        return
    
def main():
  application = webapp.WSGIApplication([('/ratings', MainHandler), ('/ratings/post', PostHandler), ('/ratings/report', ReportHandler)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)
  
if __name__ == '__main__':
  main()