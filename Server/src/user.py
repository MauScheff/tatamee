from google.appengine.ext import webapp
from datamodel import ItemLight
from datamodel import User
from google.appengine.ext.webapp import template
import logging
import os
import wsgiref.handlers

class MainHandler(webapp.RequestHandler):
  def get(self, user_id):
    user = User.get_by_key_name('key:' + user_id)
    if user:
        nickname = user.user.nickname() 
        template_values = {'user_id': user_id, 'nickname': nickname }  
        path = os.path.join(os.path.dirname(__file__), 'User.html')
        self.response.out.write(template.render(path , template_values))
        return
    else:
        self.redirect("/", true)
  
def main():
  application = webapp.WSGIApplication([(r'/user/(.*)', MainHandler)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()