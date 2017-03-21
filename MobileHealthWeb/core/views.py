# coding=utf-8
from datetime import datetime

from chartit import *
from django.shortcuts import render
from django.views.generic import TemplateView
from django.contrib.auth import get_user_model
from django.db.models import Min,Max,Avg
import time
from django.utils.timezone import localtime

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
    user = None
    if request.user.is_authenticated():
        user = request.user

    monitorings = None
    min_monitoring = None
    max_monitoring = None
    avg_heart_rate = None
    last_monitoring = None
    max_steps = None
    form = MonitoringForm(request.POST or None)
    message = None
    if form.is_valid():
        monitorings = form.buscar(user)
        if monitorings:
            min_monitoring = (monitorings.annotate(Min('heart_rate')).order_by('heart_rate')[0])
            max_monitoring = (monitorings.annotate(Max('heart_rate')).latest('heart_rate'))
            avg_heart_rate = int((monitorings.values_list('heart_rate').aggregate(Avg('heart_rate')))['heart_rate__avg'])
            last_monitoring = (monitorings.annotate(Max('date_time')).latest('date_time'))
            max_steps = (monitorings.annotate(Max('steps')).latest('steps'))
        else:
            message = "Não foram encontrados dados para a data informada!"

    chart_heart_rate = build_chart(monitorings)
    chart_steps = build_chart_steps(monitorings)
    context = {
        'form': form,
        'monitorings': monitorings,
        'min_monitoring' : min_monitoring,
        'max_monitoring' : max_monitoring,
        'avg_heart_rate' : avg_heart_rate,
        'last_monitoring' : last_monitoring,
        'max_steps' : max_steps,
        'charts' :  [chart_heart_rate, chart_steps],
        'message' : message,
    }
    return render(request, 'monitoring.html', context)

def build_chart(monitorings):
    cht = None
    if monitorings:
        ds = DataPool(
            series=
            [{'options': {
                'source': monitorings},
                'terms': [
                    ('date_time'),
                    'heart_rate']}
            ])

        cht = Chart(
            datasource=ds,
            series_options=
            [{'options': {
                'type': 'line',
                'stacking': False},
                'terms': {
                    'date_time': ['heart_rate']
                }}],
            chart_options=
            {'title': {
                'text': ' '},
                'xAxis': {
                    'title': {
                        'text': 'Horário'}}},
            x_sortf_mapf_mts = (None, lambda i: localtime(i).strftime("%H:%M"), False),
        )

    return cht

def build_chart_steps(monitorings):
    cht = None
    if monitorings:
        ds = DataPool(
            series=
            [{'options': {
                'source': monitorings},
                'terms': [
                    ('date_time'),
                    'steps']}
            ])

        cht = Chart(
            datasource=ds,
            series_options=
            [{'options': {
                'type': 'line',
                'stacking': False},
                'terms': {
                    'date_time': ['steps']
                }}],
            chart_options=
            {'title': {
                'text': ' '},
                'xAxis': {
                    'title': {
                        'text': 'Horário'}}},
            x_sortf_mapf_mts = (None, lambda i: localtime(i).strftime("%H:%M"), False),
        )

    return cht
