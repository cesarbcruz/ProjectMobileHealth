# -*- coding: utf-8 -*-
# Generated by Django 1.9.6 on 2017-04-14 17:56
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('accounts', '0004_auto_20170414_1430'),
    ]

    operations = [
        migrations.AddField(
            model_name='user',
            name='birth_date',
            field=models.DateField(null=True, verbose_name='Data de Nascimento'),
        ),
    ]