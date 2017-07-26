# Mobile Helth Web

-Heroku Deploy-

heroku login <br />
heroku create mobilehealthweb <br />

git init <br />
heroku git:remote -a nome-da-sua-app <br />
git add . <br />
git commit -am "Commit inicial" <br />
git push heroku master <br />

-Configure email- <br />
heroku config:set EMAIL_HOST_PASSWORD=<password email> <br />
#Solution ERROR 500 send email: https://accounts.google.com/DisplayUnlockCaptcha <br />

-Configure database- <br />
heroku run python manage.py migrate <br />

-Configure user admin- <br />
heroku run python manage.py createsuperuser <br />
