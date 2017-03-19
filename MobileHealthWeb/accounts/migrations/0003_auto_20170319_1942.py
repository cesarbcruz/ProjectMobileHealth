# -*- coding: utf-8 -*-
# Generated by Django 1.9.6 on 2017-03-19 22:42
from __future__ import unicode_literals

import django.core.validators
from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('accounts', '0002_user_img_url'),
    ]

    operations = [
        migrations.AlterField(
            model_name='user',
            name='img_url',
            field=models.CharField(blank=True, max_length=5000, validators=[django.core.validators.URLValidator()], verbose_name='Foto (URL)'),
        ),
    ]
