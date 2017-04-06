import django_filters
from django.core.urlresolvers import reverse_lazy
from django.utils.datetime_safe import datetime
from rest_framework import viewsets
from .models import Monitoring, Message
from .serializers import MonitoringSerializer, MessageSerializer
from rest_framework import filters
from django.db import models as django_models
from django.views.generic import CreateView, TemplateView


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

    filter_overrides = {
        django_models.DateTimeField: {
            'filter_class': django_filters.IsoDateTimeFilter
        },
    }

class MessageViewSet(viewsets.ModelViewSet):
    queryset = Message.objects.all()
    serializer_class = MessageSerializer
    filter_backends = (filters.SearchFilter, filters.OrderingFilter, filters.DjangoFilterBackend)
    search_fields = ('subject', 'msg')
    ordering_fields = ('date_time',)
    filter_fields = ('issuer__id', 'recipient__id', 'date_time')
    filter_class = MessageFilter

class MessageView(CreateView):
    model = Message
    template_name = 'api/sendmessage.html'
    success_url = reverse_lazy('api:sucessmessage')
    fields = ['recipient', 'subject','msg']

    def form_valid(self, form):
        form.instance.date_time = datetime.now()
        form.instance.issuer = self.request.user
        form.save()
        return super(MessageView, self).form_valid(form)

sendmessage = MessageView.as_view()


class SucessMessage(TemplateView):
    template_name = 'api/sucessmessage.html'

sucessmessage = SucessMessage.as_view()
