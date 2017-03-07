# coding=utf-8

from django.shortcuts import render
from django.http import HttpResponse
from django.core.mail import send_mail
from django.conf import settings
from django.core.urlresolvers import reverse_lazy
from django.contrib.auth.forms import UserCreationForm
from django.views.generic import View, TemplateView, CreateView
from django.contrib.auth import get_user_model
from django.db.models import Min,Max,Avg

from .forms import ContactForm, MonitoringForm

User = get_user_model()


class IndexView(TemplateView):

    template_name = 'index.html'


index = IndexView.as_view()


def contact(request):
    success = False
    form = ContactForm(request.POST or None)
    if form.is_valid():
        form.send_mail()
        success = True
    context = {
        'form': form,
        'success': success
    }
    return render(request, 'contact.html', context)


def apresentacao(request):
    return render(request, 'presentation.html')

def solucao(request):
    return render(request, 'solution.html')

def monitoramento(request):
    monitorings = None
    min_heart_rate = None
    max_heart_rate = None
    avg_heart_rate = None
    last_monitoring = None
    form = MonitoringForm(request.POST or None)
    if form.is_valid():
        monitorings = form.buscar()
        if monitorings:
            min_heart_rate = (monitorings.values_list('heart_rate').annotate(Min('heart_rate')).order_by('heart_rate')[0])[0]
            max_heart_rate = (monitorings.values_list('heart_rate').annotate(Max('heart_rate')).latest('heart_rate'))[0]
            avg_heart_rate = int((monitorings.values_list('heart_rate').aggregate(Avg('heart_rate')))['heart_rate__avg'])
            last_monitoring = (monitorings.annotate(Max('date_time')).latest('date_time'))
            print(last_monitoring.date_time)

    context = {
        'form': form,
        'monitorings': monitorings,
        'min_heart_rate' : min_heart_rate,
        'max_heart_rate' : max_heart_rate,
        'avg_heart_rate' : avg_heart_rate,
        'last_monitoring' : last_monitoring,
    }
    return render(request, 'monitoring.html', context)
