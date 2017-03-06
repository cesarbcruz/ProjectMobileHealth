from accounts.models import User
from django.db import models
from django.utils.timezone import localtime

class Monitoring(models.Model):
    date_time = models.DateTimeField('Data/Hora', blank=False)
    heart_rate = models.IntegerField('Frequência Cardíaca', blank=False)
    user = models.ForeignKey(User, verbose_name = u'Paciente', blank=False)
    latitude = models.FloatField(blank=True, null=True)
    longitude = models.FloatField(blank=True, null=True)

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

