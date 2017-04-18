from django.conf.urls import url, include
from django.contrib import admin
from django.contrib.auth.views import login, logout

from core import views


urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^contato/$', views.contact, name='contact'),
    url(r'^entrar/$', login, {'template_name': 'login.html'}, name='login'),
    url(r'^sair/$', logout, {'next_page': 'index'}, name='logout'),
    url(r'^apresentacao/', views.apresentacao, name='presentation'),
    url(r'^solucao/', views.solucao, name='solution'),
    url(r'^monitoramento/', views.monitoramento, name='monitoring'),
    url(r'^emergencia/', views.emergencia, name='emergency'),
    url(r'^conta/', include('accounts.urls', namespace='accounts')),
    url(r'^admin/', admin.site.urls),
    url(r'^api/', include('api.urls', namespace='api')),
]
