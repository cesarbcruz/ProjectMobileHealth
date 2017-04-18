# -*- coding: utf-8 -*-
# Generated by Django 1.9.6 on 2017-03-24 17:27
from __future__ import unicode_literals

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('api', '0005_monitoring_steps'),
    ]

    operations = [
        migrations.CreateModel(
            name='Message',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('date_time', models.DateTimeField(verbose_name='Data/Hora')),
                ('subject', models.EmailField(max_length=254, verbose_name='Assunto')),
                ('msg', models.TextField(verbose_name='Mensagem')),
                ('issuer', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='message_issuer_user', to=settings.AUTH_USER_MODEL, verbose_name='Emitente')),
                ('recipient', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, related_name='message_recipient_user', to=settings.AUTH_USER_MODEL, verbose_name='Destinatário')),
            ],
        ),
    ]
