from rest_framework import viewsets
from .models import Monitoring
from .serializers import MonitoringSerializer

class MonitoringViewSet(viewsets.ModelViewSet):
    queryset = Monitoring.objects.all()
    serializer_class = MonitoringSerializer
