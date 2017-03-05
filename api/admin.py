from django.contrib import admin
from .models import Monitoring
from django.utils.html import format_html

class MonitoringAdmin(admin.ModelAdmin):
    def show_location(self, obj):
        return format_html("<a href='{url}'>{url}</a>", url=obj.location())
    show_location.short_description = 'Localização'
    show_location.allow_tags = True

    list_display = ('user', 'date_time', 'heart_rate', 'show_location')




admin.site.register(Monitoring,MonitoringAdmin)