Versión: 2.0

Estado: OFICIAL

---

## Entidades

sources

news_articles

news_classifications

events

event_news

event_ai_analysis

generated_content

publications

users

---

## Relaciones

Source
1 → N
News

News
1 → 1
Classification

News
N → N
Event

Event
1 → N
Analysis

Event
1 → N
GeneratedContent

GeneratedContent
1 → N
Publication

User
1 → N
Contenido generado

---

## Aggregate Root Principal

Event

---

## Aggregate Root Secundario

GeneratedContent

---

## Entidad Administrativa

User