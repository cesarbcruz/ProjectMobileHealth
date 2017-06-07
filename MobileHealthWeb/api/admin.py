from django.contrib import admin
from .models import Monitoring, Message, Emergency
from django.utils.html import format_html

class MonitoringAdmin(admin.ModelAdmin):
    def show_location(self, obj):
        if obj.location():
            return format_html("<a href='http://maps.google.com/maps?q={url}'>{url}</a>", url=obj.location())
        else:
            return 'Não identificado'
    show_location.short_description = 'Localização'
    show_location.allow_tags = True

    list_display = ('user', 'date_time', 'heart_rate', 'show_location', 'steps')

admin.site.register(Monitoring,MonitoringAdmin)

class MessageAdmin(admin.ModelAdmin):
    list_display = ('issuer', 'recipient', 'date_time', 'subject', 'msg')

admin.site.register(Message,MessageAdmin)

class EmergencyAdmin(admin.ModelAdmin):
    list_display = ('date_time', 'user', 'status')

admin.site.register(Emergency,EmergencyAdmin)
