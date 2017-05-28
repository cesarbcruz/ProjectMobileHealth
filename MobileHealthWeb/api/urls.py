from api import views
from django.conf.urls import url, include
from rest_framework import routers
from .views import MonitoringViewSet, MessageViewSet, EmergencyViewSet

router = routers.DefaultRouter()
router.register(r'monitoramento', MonitoringViewSet)
router.register(r'message', MessageViewSet)
router.register(r'emergencia', EmergencyViewSet)

urlpatterns =[
    url(r'^sendmessage/$', views.sendmessage, name='sendmessage'),
    url(r'^sucessmessage/$', views.sucessmessage, name='sucessmessage'),
    url(r'^', include(router.urls)),
]