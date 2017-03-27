from rest_framework import serializers
from .models import Monitoring, Message
from django.utils import timezone

class MonitoringSerializer(serializers.ModelSerializer):
    class Meta:
        model = Monitoring
        fields = '__all__'

class CustomDateTimeField(serializers.DateTimeField):
    def to_representation(self, value):
        tz = timezone.get_default_timezone()
        # timezone.localtime() defaults to the current tz, you only
        # need the `tz` arg if the current tz != default tz
        value = timezone.localtime(value, timezone=tz)
        # py3 notation below, for py2 do:
        # return super(CustomDateTimeField, self).to_representation(value)
        return super().to_representation(value)

class MessageSerializer(serializers.ModelSerializer):
    date_time = CustomDateTimeField()
    class Meta:
        model = Message
        fields = (
             'id',
             'issuer',
             'recipient',
             'date_time',
             'subject',
             'msg',
        )