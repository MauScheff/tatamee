from google.appengine.api import urlfetch, users
from google.appengine.ext import webapp
from google.appengine.ext.webapp import template
import os
import wsgiref.handlers
import logging
from datamodel import User

class MainHandler(webapp.RequestHandler):
  def get(self):
    if not MainHandler.can_go_in(self):
        self.redirect('/static/cf.html')  
    current_user = users.get_current_user()
    user = ''
    userEmail = ''
    currency = '$'
    locale = ''
    if current_user:
      tatamee_user = User.get_by_key_name('key:' + current_user.user_id())
      if tatamee_user:
          currency = tatamee_user.currency
          locale = tatamee_user.locale
      user = current_user.nickname()
      userEmail = current_user.email()
      links = users.create_logout_url('/')
    else:
      links = users.create_login_url('/home')
    if not locale:  
        locale = MainHandler.getLocale(self)  
    template_values = {'user':user, 'userEmail':userEmail, 'links':links, 'locale':locale, 'currency': currency}
    path = os.path.join(os.path.dirname(__file__), 'Client.html')
    self.response.out.write(template.render(path , template_values))

  def getLocale(self):
      default = 'en'
      accepted = ('en', 'es', 'fr', 'sq', 'bg', 'ca', 'zh', 'hr', 'cs', 'da', 'nl', 'et', 'fi',
                  'gl', 'de', 'el', 'hi', 'hu', 'id', 'it', 'ja', 'ko', 'lv', 'lt', 'no', 'pl'
                  'pt', 'ro', 'ru', 'sr', 'sk', 'sl', 'sv', 'th', 'tr', 'uk', 'vi')
      
      headers = False
      try:
          headers = self.request.headers['Accept-Language']
      except KeyError:
          pass
      
      if headers:
          pref_langs = headers.split(',')
          for lan in pref_langs:
              if lan.strip()[:2] in accepted:
                  default = lan.strip()[:2]
                  break
              
      return default
  
  def can_go_in(self):
      user_agent = self.request.headers['User-Agent']
      if user_agent.find('MSIE') >= 0:
          if user_agent.find('chromeframe') >= 0:
              return True
          else:
              return False
      else:
          return True

def main():
  application = webapp.WSGIApplication(
      [
        ('/home', MainHandler),
        ('/', MainHandler),
      ],
      debug=False)
  wsgiref.handlers.CGIHandler().run(application)
  
if __name__ == '__main__':
  main()
