from django.conf.urls import url
from rest_framework import routers
from .views import MonitoringViewSet

router = routers.DefaultRouter()
router.register(r'monitoramento', MonitoringViewSet)

urlpatterns = router.urls