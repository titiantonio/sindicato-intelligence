-- Password comun de bootstrap/local: Admin@12345
-- Hash BCrypt generado con Spring Security para cuentas tecnicas/default.
WITH bootstrap_password AS (
    SELECT '$2a$10$7GM2nYgIU8j/7iI8EWm9pO7dr6VjpJG5nkuoNQue6mMAHWtJYfBKK'::VARCHAR(255) AS password_hash
), default_users(email, name, role) AS (
    VALUES
        ('admin@sindicato.es', 'Admin Sindicato', 'ADMIN'),
        ('n8n@sindicato.es', 'N8N Service', 'ADMIN'),
        ('editor@sindicato.es', 'Editor Sindicato', 'EDITOR')
)
INSERT INTO users (
    email,
    password_hash,
    name,
    role,
    active,
    must_change_password,
    status,
    temporary_password_expires_at,
    last_password_change_at,
    created_at,
    updated_at
)
SELECT
    default_users.email,
    bootstrap_password.password_hash,
    default_users.name,
    default_users.role,
    TRUE,
    FALSE,
    'ACTIVE',
    NULL,
    NOW(),
    NOW(),
    NOW()
FROM default_users
CROSS JOIN bootstrap_password;

INSERT INTO sources (name, url, type, priority, active)
VALUES
    ('CCOO Enseñanza', 'https://fe.ccoo.es/rss.php?canal=9500', 'RSS', 1, TRUE),
    ('STEs Intersindical', 'https://stes.es/feed/', 'RSS', 2, TRUE),
    ('BOE', 'https://www.boe.es/rss/canal.php?c=b_oposiciones', 'RSS', 3, TRUE),
    ('UGT Enseñanza', 'https://ugtserviciospublicosandalucia.es/index.php/ensenanza/ensenanza-publica/actualidad?format=feed&type=rss', 'RSS', 4, TRUE),
    ('Boja Oposiciones', 'https://www.juntadeandalucia.es/boja/distribucion/s53.xml', 'RSS', 5, TRUE),
    ('El Mundo Andalucía', 'https://e00-elmundo.uecdn.es/elmundo/rss/andalucia.xml', 'RSS', 6, TRUE),
    ('SIPRI y Personal Docente', 'https://www.juntadeandalucia.es/educacion/portals/web/ced/personal-docente/-/rss/journal/10156', 'RSS', 7, TRUE),
    ('Málaga Hoy', 'https://www.malagahoy.es/rss/', 'RSS', 8, TRUE),
    ('Ordenación Educativa y Secundaria', 'https://www.juntadeandalucia.es/educacion/portals/web/ced/novedades/-/rss/journal/10156', 'RSS', 9, TRUE),
    ('Formación Profesional (FP)', 'https://www.juntadeandalucia.es/educacion/portals/web/ced/formacion-profesional/-/rss/journal/10156', 'RSS', 10, TRUE),
    ('Cualificaciones Profesionales (IACP)', 'https://www.juntadeandalucia.es/educacion/portals/web/iacp/-/rss/journal/10156', 'RSS', 11, TRUE),
    ('Agencia de Evaluación Educativa', 'https://www.juntadeandalucia.es/educacion/portals/web/ced/evaluacion-educativa/-/rss/journal/10156', 'RSS', 12, TRUE),
    ('Consejo Escolar de Andalucía', 'https://www.juntadeandalucia.es/educacion/portals/web/consejo-escolar-de-andalucia/-/rss/journal/10156', 'RSS', 13, TRUE),
    ('Parlamento de Andalucía (Actividad)', 'https://www.parlamentodeandalucia.es/opencms/export/portal-web-parlamento/rss/actualidad.xml', 'RSS', 14, TRUE),
    ('ANPE Andalucía', 'https://anpeandalucia.es/rss.php', 'RSS', 15, TRUE),
    ('CSIF Educación', 'https://www.csif.es/andalucia/educacion/rss', 'RSS', 16, TRUE),
    ('Cadena SER Andalucía', 'https://cadenaser.com/rss/andalucia/', 'RSS', 17, TRUE),
    ('La Vanguardia', 'https://www.lavanguardia.com/local/andalucia/rss', 'RSS', 18, TRUE),
    ('El País Educación', 'https://feeds.elpais.com/mrss-s/pages/ep/site/elpais.com/section/sociedad/portada', 'RSS', 19, TRUE),
    ('20 Minutos', 'https://www.20minutos.es/rss/', 'RSS', 20, TRUE),
    ('eldiario.es Andalucía', 'https://www.eldiario.es/rss/andalucia/', 'RSS', 21, TRUE),
    ('Granada Hoy', 'https://www.granadahoy.com/rss/', 'RSS', 22, TRUE),
    ('Diario de Sevilla', 'https://www.diariodesevilla.es/rss/', 'RSS', 23, TRUE),
    ('Huelva Información', 'https://www.huelvainformacion.es/rss/', 'RSS', 24, TRUE),
    ('Ideal', 'https://www.ideal.es/rss/2.0/', 'RSS', 25, TRUE),
    ('BOJA Oficial (Actualidad)', 'https://www.juntadeandalucia.es/boja/rss/actualidad.xml', 'RSS', 26, TRUE),
    ('El Diario de la Educación', 'https://eldiariodelaeducacion.com/feed/', 'RSS', 27, TRUE),
    ('La Opinión de Málaga', 'https://www.laopiniondemalaga.es/rss/', 'RSS', 28, TRUE),
    ('Magisnet', 'https://www.magisnet.com/feed/', 'RSS', 29, TRUE),
    ('Educación 3.0', 'https://www.educaciontrespuntocero.com/feed/', 'RSS', 30, TRUE),
    ('Educaweb', 'https://www.educaweb.com/rss/', 'RSS', 31, TRUE),
    ('Campus Educación', 'https://www.campuseducacion.com/blog/feed/', 'RSS', 32, TRUE),
    ('Docentes 2.0', 'https://docentes20.com/feed/', 'RSS', 33, TRUE),
    ('INTEF', 'https://intef.es/feed/', 'RSS', 34, TRUE),
    ('Ministerio Educación', 'https://www.educacionfpe.gob.es/rss/actualidad.xml', 'RSS', 35, TRUE),
    ('Universidad de Sevilla', 'https://www.us.es/actualidad/rss', 'RSS', 36, TRUE),
    ('Junta Andalucía Noticias', 'https://www.juntadeandalucia.es/presidencia/portavoz/rss.xml', 'RSS', 37, TRUE),
    ('Administracion.gob.es', 'https://administracion.gob.es/rss/rssEmpleoPublico.xml', 'RSS', 38, TRUE),
    ('Opospills', 'https://opospills.com/feed/', 'RSS', 39, TRUE),
    ('Opositor.com', 'https://www.opositor.com/feed/', 'RSS', 40, TRUE),
    ('MasterD Educación', 'https://www.masterd.es/blog/feed', 'RSS', 41, TRUE),
    ('Xataka Educación', 'https://www.xataka.com/tag/educacion/rss2.xml', 'RSS', 42, TRUE),
    ('Genbeta', 'https://www.genbeta.com/tag/educacion/rss2.xml', 'RSS', 43, TRUE),
    ('MuyComputer Educación', 'https://www.muycomputer.com/tag/educacion/feed/', 'RSS', 44, TRUE),
    ('Tiching Blog', 'https://blog.tiching.com/feed/', 'RSS', 45, TRUE),
    ('Google for Education Blog', 'https://blog.google/outreach-initiatives/education/rss/', 'RSS', 46, TRUE),
    ('CRUE Universidades', 'https://www.crue.org/feed/', 'RSS', 47, TRUE),
    ('Universidad Internacional de Andalucía', 'https://www.unia.es/rss.xml', 'RSS', 48, TRUE),
    ('Universidad de Málaga Noticias', 'https://www.uma.es/rss/noticias.xml', 'RSS', 49, TRUE),
    ('Europa Press', 'https://www.europapress.es/rss/rss.aspx', 'RSS', 50, TRUE),
    ('Diario Sur', 'https://www.diariosur.es/rss/2.0/', 'RSS', 51, TRUE),
    ('ABC Andalucía', 'https://www.abc.es/rss/feeds/abc_Andalucia.xml', 'RSS', 52, TRUE),
    ('Prensa Google News (Profesorado Andalucía)', 'https://news.google.com/rss/search?q=(intitle:profesorado+OR+intitle:profesores+OR+intitle:docentes+OR+intitle:maestros+OR+intitle:SIPRI+OR+intitle:interinos+OR+intitle:oposiciones+OR+intitle:educación)+%22Andaluc%C3%ADa%22+when:1d&hl=es&gl=ES&ceid=ES:es', 'RSS', 53, TRUE),
    ('CEF Oposiciones', 'https://www.cef.es/es/rss/noticias', 'RSS', 54, TRUE);

INSERT INTO automation_workflow_settings (
    workflow_code,
    enabled,
    interval_seconds,
    batch_size,
    next_run_at
) VALUES
    ('WF02_CLASSIFICATION', FALSE, 600, 1, NOW() + INTERVAL '10 minutes'),
    ('WF03_EVENT_DETECTION', FALSE, 600, 3, NOW() + INTERVAL '10 minutes'),
    ('WF04_ANALYSIS', FALSE, 900, 1, NOW() + INTERVAL '15 minutes');

INSERT INTO telegram_publication_settings (
    id,
    enabled,
    base_url,
    bot_token,
    chat_id,
    disable_web_page_preview,
    max_attachment_count,
    max_attachment_file_bytes,
    max_attachment_total_bytes
) VALUES (
    1,
    FALSE,
    'https://api.telegram.org',
    NULL,
    NULL,
    TRUE,
    10,
    20971520,
    52428800
);

INSERT INTO ai_prompt_versions (prompt_key, prompt_name, module, version, checksum, active)
VALUES
    ('WF02_CLASSIFICATION', 'Clasificacion de noticias', 'classification', '1.0.0', 'f5d89b8f7ce78e3137c6b0e789dc51d8a99625ff8d8b9af4ea6b5bd6a845621a', TRUE),
    ('WF03_EVENT_MATCHING', 'Agrupacion de eventos', 'event', '1.0.0', '8d7f41e6f77eae71b18d8d9704f87d95dba9ec4f2511d6f1d983b80b4fa6d3a4', TRUE),
    ('WF04_ANALYSIS', 'Analisis de evento', 'analysis', '1.0.0', '52a4ef08963b497595b5467ff3d3d2011cb1f983e9373c4e6ac94cd87bb24a68', TRUE),
    ('WF05_CONTENT', 'Generacion de contenido Telegram', 'content', '1.0.0', '7a9ca36bd2836edbdc2c696e003e41c70d6fd02c7acef27cd98c71555d2054b1', TRUE);

INSERT INTO ai_provider_settings (
    provider_code,
    display_name,
    enabled
) VALUES
    ('deterministic', 'Determinista local', FALSE),
    ('gemini', 'Google Gemini', FALSE);

INSERT INTO ai_workflow_settings (
    workflow_code,
    provider_code,
    model_name,
    temperature,
    max_output_tokens,
    cooldown_seconds
) VALUES
    ('WF02_CLASSIFICATION', 'deterministic', 'deterministic-classification', 0.2, 1024, 60),
    ('WF03_EVENT_MATCHING', 'deterministic', 'deterministic-event-matching', 0.2, 1024, 60),
    ('WF04_ANALYSIS', 'deterministic', 'deterministic-analysis', 0.2, 1024, 60),
    ('WF05_CONTENT', 'deterministic', 'deterministic-content', 0.2, 1024, 60);
