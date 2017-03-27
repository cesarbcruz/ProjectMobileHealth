import django_filters
from rest_framework import viewsets
from .models import Monitoring, Message
from .serializers import MonitoringSerializer, MessageSerializer
from rest_framework import filters
from django.db import models as django_models


class MonitoringViewSet(viewsets.ModelViewSet):
    queryset = Monitoring.objects.all()
    serializer_class = MonitoringSerializer

class MessageFilter(filters.FilterSet):
    class Meta:
        model = Message
        fields = {
            'date_time': ('lt', 'gt'),
            'recipient__id': ['exact'],
            'issuer__id' : ['exact']
        }

class MessageViewSet(viewsets.ModelViewSet):
    queryset = Message.objects.all()
    serializer_class = MessageSerializer
    filter_backends = (filters.SearchFilter, filters.OrderingFilter, filters.DjangoFilterBackend)
    search_fields = ('subject', 'msg')
    ordering_fields = ('date_time',)
    filter_fields = ('issuer__id', 'recipient__id', 'date_time')
    filter_class = MessageFilter

