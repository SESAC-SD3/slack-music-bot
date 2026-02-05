#!/bin/bash
set -e

# Update system
dnf update -y

# Install Docker
dnf install -y docker
systemctl enable docker
systemctl start docker

# Install Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Install Git
dnf install -y git

# Install Nginx
dnf install -y nginx
systemctl enable nginx

# Install Certbot
dnf install -y certbot python3-certbot-nginx

# Create app directory
mkdir -p /opt/music-bot
cd /opt/music-bot

# Create environment file
cat > .env << 'EOF'
SLACK_BOT_TOKEN=${slack_bot_token}
SLACK_SIGNING_SECRET=${slack_signing_secret}
DEFAULT_VIDEO_URL=${default_video_url}
YOUTUBE_API_KEY=${youtube_api_key}
EOF

# Create docker-compose.yml
cat > docker-compose.yml << 'COMPOSE'
services:
  app:
    image: ghcr.io/$${GITHUB_REPOSITORY}:latest
    ports:
      - "127.0.0.1:8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/musicbot?useSSL=false&allowPublicKeyRetrieval=true
      - SPRING_DATASOURCE_USERNAME=musicbot
      - SPRING_DATASOURCE_PASSWORD=musicbot
      - SLACK_BOT_TOKEN=$${SLACK_BOT_TOKEN}
      - SLACK_SIGNING_SECRET=$${SLACK_SIGNING_SECRET}
      - DEFAULT_VIDEO_URL=$${DEFAULT_VIDEO_URL}
      - YOUTUBE_API_KEY=$${YOUTUBE_API_KEY}
    depends_on:
      db:
        condition: service_healthy
    restart: unless-stopped

  db:
    image: mysql:8.0
    environment:
      - MYSQL_DATABASE=musicbot
      - MYSQL_USER=musicbot
      - MYSQL_PASSWORD=musicbot
      - MYSQL_ROOT_PASSWORD=root
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 5s
      timeout: 5s
      retries: 5
    restart: unless-stopped

volumes:
  mysql_data:
COMPOSE

# Configure Nginx
cat > /etc/nginx/conf.d/music-bot.conf << NGINX
server {
    listen 80;
    server_name ${domain_name};

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_read_timeout 86400;
    }
}
NGINX

# Start Nginx
systemctl start nginx

# Create SSL setup script
cat > /opt/music-bot/setup-ssl.sh << SSLSCRIPT
#!/bin/bash
certbot --nginx -d ${domain_name} --non-interactive --agree-tos --email admin@5chang2.com --redirect
SSLSCRIPT
chmod +x /opt/music-bot/setup-ssl.sh

echo "Setup complete. Run 'docker-compose up -d' after configuring GitHub Container Registry access."
echo "After app is running, execute '/opt/music-bot/setup-ssl.sh' to enable HTTPS."
