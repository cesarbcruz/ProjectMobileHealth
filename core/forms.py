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
    autorefresh = forms.IntegerField(label='Atualizar resultado automaticamente (minuto)', initial=1, min_value=1, max_value=60)

    def getPkUser(self, user):
        return user.pk if user else 1;

    def buscar(self, user):
        return Monitoring.objects.filter(date_time__date=self.cleaned_data['date'], user_id=self.getPkUser(user))

    def dataSteps(self, user, max_steps):
        steps = 0
        if max_steps.steps:
            steps = max_steps.steps

        steps_pending = 10000 - steps

        sql = ""

        if steps > 0:
            sql = "SELECT 'Concluída' as label , "+str(steps)+" as passos, 1 as id"

        if steps_pending > 0:
            if sql:
                sql += " union "
            sql += " SELECT 'Pendente' as label , " + str(steps_pending) + " as passos, 2 as id"
        return self.buscar(user).raw(sql)


