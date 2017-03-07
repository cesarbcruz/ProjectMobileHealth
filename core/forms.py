# coding=utf-8
from api.models import Monitoring
from django import forms
from django.core.mail import send_mail
from django.conf import settings


class ContactForm(forms.Form):

    name = forms.CharField(label='Nome')
    email = forms.EmailField(label='E-mail')
    message = forms.CharField(label='Mensagem', widget=forms.Textarea())

    def send_mail(self):
        name = self.cleaned_data['name']
        email = self.cleaned_data['email']
        message = self.cleaned_data['message']
        message = 'Nome: {0}\nE-mail:{1}\n{2}'.format(name, email, message)
        send_mail(
            'Contato do Mobile Health', message, settings.DEFAULT_FROM_EMAIL,
            [settings.DEFAULT_FROM_EMAIL]
        )
class MonitoringForm(forms.Form):
    date = forms.DateField(label='Data',widget=forms.widgets.DateInput(attrs={'type': 'date'}))

    def buscar(self, user):
        return Monitoring.objects.filter(date_time__date=self.cleaned_data['date'], user=user)