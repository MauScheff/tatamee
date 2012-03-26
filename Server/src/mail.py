from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext import webapp
from datamodel import Item
from datamodel import User
from datamodel import Comment
from google.appengine.api import mail
import wsgiref.handlers
import simplejson
import logging

class MainHandler(webapp.RequestHandler):
  def post(self):
    sender = self.request.get("from")
    id = self.request.get("id")
    message = self.request.get("message")
    comment = self.request.get("comment")
    
    item_key = db.Key(id)
    item = db.get(item_key)
    send_to = item.parent().user.email()
    email_message = message
    
    if (item.parent().user == users.get_current_user()):
       new_comment = Comment(parent=item ,message=message[0:400], poster='seller')
       new_comment.put()
       item.comments += 1
       item.put()
       self.response.out.write("Success")
       self.response.set_status(201)
       return 
    
    if not users.get_current_user():
      if not mail.is_email_valid(sender):
        invalid_reason = mail.invalid_email_reason(sender, sender)
        self.response.out.write("<ERROR EXCEPTION>")
        return
        #TODO: Translate message
        #throw exception in client with message
      email_message += '''
      
*** IMPORTANT ***
Please do not reply back to this message.
Instead, you may write an email to %s
''' % sender
      sender = 'mail@tatamee.com'
    else:
      sender = users.get_current_user().email()
      
    if comment:
        if (users.get_current_user()):
            new_comment = Comment(parent=item ,message=message[0:400], poster=users.get_current_user().nickname())
        else:   
            new_comment = Comment(parent=item ,message=message[0:400], poster='anonymous')
        new_comment.put()
        item.comments += 1
        item.put()
      
    mail.send_mail(sender, send_to, 'Tatamee Buyer Notification [%s]' % item.title, email_message)    
    logging.info('Email sent to: %s, by: %s' % (send_to, sender))
    self.response.out.write("Success")
    self.response.set_status(201)
    
def main():
  application = webapp.WSGIApplication([('/mail', MainHandler)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)
  
if __name__ == '__main__':
  main()