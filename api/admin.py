from django.contrib import admin
from .models import Monitoring
from geopy.geocoders import GoogleV3
from django.utils.html import format_html

class MonitoringAdmin(admin.ModelAdmin):
    def show_location(self, obj):
        if obj.location():
            return format_html("<a href='http://maps.google.com/maps?q={url}'>{url}</a>", url=obj.location())
        else:
            return 'Não identificado'
    show_location.short_description = 'Localização'
    show_location.allow_tags = True

    # def show_address(self, obj):
    #     geolocator = GoogleV3()
    #     location = None
    #     if obj.location():
    #         location = geolocator.reverse(obj.location())
    #     if location:
    #         address = ''
    #         for l in location:
    #             address += l.address + "\n"
    #         return format_html("<textarea readonly>" + address + "</textarea>")
    #     else:
    #         return 'Não identificado'
    # show_address.short_description = 'Endereço aproximado '
    # show_address.allow_tags = True
    # 'show_address'

    list_display = ('user', 'date_time', 'heart_rate', 'show_location', )





admin.site.register(Monitoring,MonitoringAdmin)