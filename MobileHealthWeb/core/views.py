# coding=utf-8
from api.models import Monitoring, Emergency, STATUS_CHOICES, DONE
from chartit import *
from django.shortcuts import render
from django.contrib.auth import get_user_model
from django.db.models import Min, Max, Avg
from django.utils.timezone import localtime, now
from .forms import ContactForm, MonitoringForm
from django.views.generic import TemplateView
import traceback
import logging

User = get_user_model()


class IndexView(TemplateView):

    template_name = 'index.html'


index = IndexView.as_view()


def contact(request):
    success = False
    form = ContactForm(request.POST or None)
    if form.is_valid():
        try:
            form.send_mail()
            success = True
        except Exception as e:
            logging.error(traceback.format_exc())
            success = False
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
    example = None
    if not request.user.is_authenticated():
        example = Monitoring.objects.filter(heart_rate__gt=0, steps__gt=0, date_time__date__lte=now()).latest('date_time')

    monitorings = None
    min_monitoring = None
    max_monitoring = None
    avg_heart_rate = None
    last_monitoring = None
    max_steps = None
    chart_heart_rate = None
    chart_steps = None
    form = MonitoringForm(request.POST or None, initial={'user': request.user})
    message = None
    if form.is_valid():
        monitorings = form.buscar()
        if monitorings:
            min_monitoring = (monitorings.annotate(Min('heart_rate')).order_by('heart_rate')[0])
            max_monitoring = (monitorings.annotate(Max('heart_rate')).latest('heart_rate'))
            avg_heart_rate = int((monitorings.values_list('heart_rate').aggregate(Avg('heart_rate')))['heart_rate__avg'])
            last_monitoring = (monitorings.annotate(Max('date_time')).latest('date_time'))
            max_steps = (monitorings.annotate(Max('steps')).latest('steps'))
            chart_heart_rate = build_chart(monitorings)
            chart_steps = build_chart_steps(form.dataSteps(max_steps))
        else:
            message = "Não foram encontrados dados para o paciente e data informada!"


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
        'example' : example,
    }
    return render(request, 'monitoring.html', context)


def emergencia(request):
    ids_emergency = Emergency.objects.filter(status__lt=DONE).values('user').annotate(max_id=Max('id')).order_by("user")
    emergencies = Emergency.objects.filter(id__in=ids_emergency.values_list('max_id', flat=True))
    id_monitorings = Monitoring.objects.filter(user__in=ids_emergency.values_list('user', flat=True)).values('user').annotate(max_id=Max('id')).order_by("user")
    monitorings = Monitoring.objects.filter(id__in=id_monitorings.values_list('max_id', flat=True))

    emergencies_monitorings = zip(emergencies, monitorings)

    context = {
        'emergencies_monitorings' : emergencies_monitorings
    }
    return render(request, 'emergency.html', context)


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


def getColorSteps(dataSteps):
    color = []
    for line in dataSteps:
        if line.label == 'Pendente':
            color.append('#ff5050')
        else:
            color.append('#00cc00')
    return color



def build_chart_steps(dataSteps):
    cht = None
    if dataSteps:
        ds = DataPool(
            series=
            [{'options': {
                'source': dataSteps},
                'terms': [
                    'label',
                    'passos']}
            ])

        color = getColorSteps(dataSteps)


        cht = Chart(
            datasource=ds,
            series_options=
            [{'options': {
                'type': 'pie',
                'stacking': False, 'colors': color},
                'terms': {
                    'label': [
                        'passos']
                }}],
            chart_options=
            {'title': {
                'text': 'Meta: 10.000 passos por dia'},
                'tooltip': {
                    'pointFormat': '{point.y:.0f} {series.name}: <b>{point.percentage:.1f}%</b>'
                },
            })
    return cht