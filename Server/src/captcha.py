from os import environ
import urllib
from google.appengine.api import urlfetch
from google.appengine.ext import webapp
import wsgiref.handlers

"""
    Adapted from http://pypi.python.org/pypi/recaptcha-client
    to use with Google App Engine
    by Joscha Feth <joscha@feth.com>
    Version 0.1
"""

API_SSL_SERVER  = "https://api-secure.recaptcha.net"
API_SERVER      = "http://api.recaptcha.net"
VERIFY_SERVER   = "api-verify.recaptcha.net"

class RecaptchaResponse(object):
    def __init__(self, is_valid, error_code=None):
        self.is_valid   = is_valid
        self.error_code = error_code

def displayhtml (public_key,
                 use_ssl = False,
                 error = None):
    """Gets the HTML to display for reCAPTCHA

    public_key -- The public api key
    use_ssl -- Should the request be sent over ssl?
    error -- An error message to display (from RecaptchaResponse.error_code)"""

    error_param = ''
    if error:
        error_param = '&error=%s' % error

    if use_ssl:
        server = API_SSL_SERVER
    else:
        server = API_SERVER

    return """
    <html><body>
    <div id="captcha"></div>
    <div id=loading><img src="/images/loading.gif"></img></div>
    <script type="text/javascript" src="%(ApiServer)s/js/recaptcha_ajax.js"></script>
    <script>
    function removeloading()
    {
      document.getElementById('loading').innerHTML = "";
    } 
    Recaptcha.create("%(PublicKey)s",
    "captcha", {
      theme: "white",
      lang: "en",
      callback: removeloading(),
    });
    </script>
    </body></html>
""" % {
        'ApiServer' : server,
        'PublicKey' : public_key,
        'ErrorParam' : error_param,
        }


def submit (recaptcha_challenge_field,
            recaptcha_response_field,
            private_key,
            remoteip):
    """
    Submits a reCAPTCHA request for verification. Returns RecaptchaResponse
    for the request

    recaptcha_challenge_field -- The value of recaptcha_challenge_field from the form
    recaptcha_response_field -- The value of recaptcha_response_field from the form
    private_key -- your reCAPTCHA private key
    remoteip -- the user's ip address
    """

    if not (recaptcha_response_field and recaptcha_challenge_field and
            len (recaptcha_response_field) and len (recaptcha_challenge_field)):
        return RecaptchaResponse (is_valid = False, error_code = 'incorrect-captcha-sol')
    
    headers = {
               'Content-type':  'application/x-www-form-urlencoded',
               "User-agent"  :  "reCAPTCHA GAE Python"
               }         
    
    params = urllib.urlencode ({
        'privatekey': private_key,
        'remoteip' : remoteip,
        'challenge': recaptcha_challenge_field,
        'response' : recaptcha_response_field,
        })

    httpresp = urlfetch.fetch(
                   url      = "http://%s/verify" % VERIFY_SERVER,
                   payload  = params,
                   method   = urlfetch.POST,
                   headers  = headers
                    )     
    
    if httpresp.status_code == 200:
        # response was fine
        
        # get the return values
        return_values = httpresp.content.splitlines();
        
        # get the return code (true/false)
        return_code = return_values[0]
        
        if return_code == "true":
            # yep, filled perfectly
            return RecaptchaResponse (is_valid=True)
        else:
            # nope, something went wrong
            return RecaptchaResponse (is_valid=False, error_code = return_values [1])
    else:
        # recaptcha server was not reachable
        return RecaptchaResponse (is_valid=False, error_code = "recaptcha-not-reachable")
    
class CaptchaHandler(webapp.RequestHandler):
  def get(self):
    chtml = displayhtml(
              public_key = "6LdIdwgAAAAAAARG_GYNF_8RggdRAsANZ1h74zY0",
              use_ssl = False,
              error = None)    
    self.response.out.write(chtml)

def main():
  application = webapp.WSGIApplication([('/captcha', CaptchaHandler)], debug=False)
  wsgiref.handlers.CGIHandler().run(application)
  
if __name__ == '__main__':
  main()    