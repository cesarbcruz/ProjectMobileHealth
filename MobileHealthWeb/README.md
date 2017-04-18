# Mobile Helth Web

-Heroku Deploy-

heroku login
heroku create mobilehealthweb

git init
heroku git:remote -a nome-da-sua-app
git add .
git commit -am "Commit inicial"
git push heroku master

-Configure email-
heroku config:set EMAIL_HOST_PASSWORD=<password email>
#Solution ERROR 500 send email: https://accounts.google.com/DisplayUnlockCaptcha

-Configure database-
heroku run python manage.py migrate

-Configure user admin-
heroku run python manage.py createsuperuser
