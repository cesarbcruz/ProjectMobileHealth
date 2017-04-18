from rest_framework import serializers
from .models import Monitoring, Message

class MonitoringSerializer(serializers.ModelSerializer):
    class Meta:
        model = Monitoring
        fields = '__all__'

class MessageSerializer(serializers.ModelSerializer):
    issuer_name = serializers.ReadOnlyField()
    issuer_img = serializers.ReadOnlyField()

    class Meta:
        model = Message
        fields = ('id',
                  'issuer',
                  'recipient',
                  'date_time',
                  'subject',
                  'msg',
                  'issuer_name',
                  'issuer_img',
                  )