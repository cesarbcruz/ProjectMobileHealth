from accounts.models import User
from django.db import models
from django.utils.timezone import localtime
from geopy.geocoders import GoogleV3

class Monitoring(models.Model):
    date_time = models.DateTimeField('Data/Hora', blank=False)
    heart_rate = models.IntegerField('Frequência Cardíaca', blank=False)
    user = models.ForeignKey(User, verbose_name = u'Paciente', blank=False)
    latitude = models.FloatField(blank=True, null=True)
    longitude = models.FloatField(blank=True, null=True)
    steps = models.IntegerField('Passos', blank=True, null=True)
    emergency = models.BooleanField(default=False)

    class Meta:
        verbose_name = "Monitoramento"
        verbose_name_plural = "Monitoramentos"

    def __str__(self):
        DATE_FORMAT = "%d/%m/%Y"
        TIME_FORMAT = "%H:%M:%S"
        return '%s | %s' % (self.user, localtime(self.date_time).strftime("%s %s" % (DATE_FORMAT, TIME_FORMAT)))

    def location(self):
        if (self.latitude and self.longitude) and (self.latitude != 0 and self.longitude != 0):
            return '%s,%s' %  (self.latitude, self.longitude)
        else:
            return None;

    def address(self):
        try:
            geolocator = GoogleV3()
            locations = None
            if self.location:
                locations = geolocator.reverse(self.location())
            if locations:
                address = ''
                for l in locations:
                    address += l.address + "<br>"
                return address
            else:
                return 'Não identificado'
        except Exception as e:
            return "Não identificado (Erro: {0})".format(e)

    def map(self):
        return "<iframe width='100%' height='450' frameborder='0' style='border:0' src='https://www.google.com/maps/embed/v1/place?q="+self.location()+"&amp;key=AIzaSyATsuuNRa7lkmJ2jgyNjLg7vvS8wb7nU-g'></iframe>"

class Message(models.Model):
    issuer  = models.ForeignKey(User, verbose_name = u'Emitente', related_name="message_issuer_user", blank=False)
    recipient = models.ForeignKey(User, verbose_name=u'Destinatário', related_name="message_recipient_user", blank=False)
    date_time = models.DateTimeField('Data/Hora', blank=False)
    subject = models.CharField('Assunto', max_length=1000)
    msg = models.TextField('Mensagem', blank=False)

    def __str__(self):
        return '%s | %s | %s' % (self.issuer, self.recipient, self.subject )

    @property
    def issuer_name(self):
        return self.issuer.name

    @property
    def issuer_img(self):
        return self.issuer.img_url


