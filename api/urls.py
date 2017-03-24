from django.conf.urls import url
from rest_framework import routers
from .views import MonitoringViewSet, MessageViewSet

router = routers.DefaultRouter()
router.register(r'monitoramento', MonitoringViewSet)
router.register(r'message', MessageViewSet)

urlpatterns = router.urls