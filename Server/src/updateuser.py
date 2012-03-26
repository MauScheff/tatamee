# DONT REMOVE MAGIC COMMENT. This Python file uses the following encoding: utf-8
from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext import webapp
from datamodel import User
from datamodel import CityCounter
from django.utils import simplejson
import wsgiref.handlers
import geo
import logging

# TODO: ADD RPC SECURTIY STUFF (PRIVATE METHODS) on 
# http://code.google.com/appengine/articles/rpc.html 
class UpdateUser(webapp.RequestHandler):
  def post(self):
    user_id = users.get_current_user().user_id()
    user = User.get_by_key_name('key:' + user_id)
    if not user:
      location = db.GeoPt(float(self.request.get('lat')), float(self.request.get('lng')))
      square = geo.hashArea(location)
      user = User(key_name='key:' + user_id, home=location, area=square)
      user.user = users.get_current_user()
      country = self.request.get('country')
      #Can use memcache here
      if geo.country_currency.has_key(country):
          user.currency = geo.country_currency[country].decode('utf-8')
      else:
          user.currency = '$'
    else:
      user.home = db.GeoPt(float(self.request.get('lat')), float(self.request.get('lng')))
      user.area = geo.hashArea(user.home)
      country = self.request.get('country')
      if geo.country_currency.has_key(country):
          user.currency = geo.country_currency[country].decode('utf-8')
      else:
          user.currency = '$'
      
      #TODO: Update location of all user items? 
          
#      TODO: COunter not working
    #  if not city.users:
    #      city.users = 1
    #  else:
    #      city.users = city.users + 1
    #user.city = city
    
    user.put()
    logging.info('wrote user to database = ' + str(user.key()))
    json_response = simplejson.dumps({'currency': user.currency})
    self.response.out.write(json_response)

class GetUser(webapp.RequestHandler):
  def get(self):
    user_id = users.get_current_user().user_id()
    user = User.get_by_key_name('key:' + user_id)
    if not user:
      self.response.out.write('false')
    else:
      json_response = simplejson.dumps({'user': user.user.nickname(), 'timestamp': str(user.timestamp), 'lat': user.home.lat, 'lon': user.home.lon, 'currency': user.currency})
      self.response.out.write(json_response)
      
class UpdateCurrency(webapp.RequestHandler):
    def post(self):
        currency = self.request.get('currency')
        user_id = users.get_current_user().user_id()
        user = User.get_by_key_name('key:' + user_id)
        user.currency = currency
        user.put()
        logging.info('Changed user currency = ' + user.currency)
        json_response = simplejson.dumps({'currency': user.currency})
        self.response.out.write(json_response)
        
class UpdateLocale(webapp.RequestHandler):
    def post(self):
        locale = self.request.get('locale')
        user_id = users.get_current_user().user_id()
        user = User.get_by_key_name('key:' + user_id)
        user.locale = locale
        user.put()
        logging.info('Changed user locale = ' + user.locale)
        json_response = simplejson.dumps({'locale': user.locale})
        self.response.out.write(json_response)        
        
class GetCurrencies(webapp.RequestHandler):
    def get(self):
        json_response = simplejson.dumps({'currencies': list(set(geo.country_currency.itervalues()))})
        self.response.out.write(json_response)
    
def main():
  application = webapp.WSGIApplication([('/updateuser', UpdateUser)
                                        ,('/getuser', GetUser)
                                        ,('/updatelocale', UpdateLocale)
                                        ,('/updatecurrency', UpdateCurrency),], debug=False)
  wsgiref.handlers.CGIHandler().run(application)

if __name__ == '__main__':
  main()