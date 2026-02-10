# 🐳 Netfliz API - Docker Deployment Guide

## 📋 Table of Contents
- [Prerequisites](#-prerequisites)
- [Quick Start (< 5 minutes)](#-quick-start--5-minutes)
- [Environment Variables](#-environment-variables)
- [Architecture](#-architecture)
- [Commands Reference](#-commands-reference)
- [Production Considerations](#-production-considerations)
- [Troubleshooting](#-troubleshooting)

---

## ✅ Prerequisites

| Component | Minimum Version | Check Command |
|-----------|-----------------|---------------|
| Docker | 24.0+ | `docker --version` |
| Docker Compose | 2.20+ | `docker compose version` |
| RAM | 4GB+ | - |
| Disk | 10GB+ | - |

---

## 🚀 Quick Start (< 5 minutes)

### Option 1: Using Scripts (Recommended)

**Windows (PowerShell):**
```powershell
cd d:\Netfliz\netfliz-api
.\scripts\deploy.ps1
```

**Linux/Mac:**
```bash
cd /path/to/netfliz-api
chmod +x scripts/deploy.sh
./scripts/deploy.sh
```

### Option 2: Manual Deployment

```bash
# 1. Clone repository (if not already)
git clone <repository-url>
cd netfliz-api

# 2. Create environment file
cp .env.example .env

# 3. Edit .env with your configuration
# (At minimum, change passwords and JWT_SECRET_KEY)

# 4. Build and start all services
docker compose up -d --build

# 5. Check status
docker compose ps

# 6. View logs
docker compose logs -f
```

### Option 3: Pull from Registry (Production)

If you have a Docker registry:

```bash
# 1. Create environment file
cp .env.example .env
# Edit .env with production values

# 2. Pull and run
docker compose pull
docker compose up -d
```

---

## 🔐 Environment Variables

### Required Configuration

| Variable | Description | Example |
|----------|-------------|---------|
| `JWT_SECRET_KEY` | JWT signing key (min 32 chars) | `openssl rand -hex 32` |
| `POSTGRES_PASSWORD` | Database password | `your_secure_password` |
| `REDIS_PASSWORD` | Redis password | `your_secure_password` |

### Optional Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8080` | API exposed port |
| `POSTGRES_DB` | `netfliz` | Database name |
| `POSTGRES_USER` | `netfliz` | Database user |
| `POSTGRES_PORT` | `5432` | PostgreSQL exposed port |
| `REDIS_PORT` | `6379` | Redis exposed port |
| `CORS_ALLOWED_ORIGINS` | `*` | CORS patterns |

### Storage Configuration (Optional)

| Variable | Description |
|----------|-------------|
| `FIREBASE_STORAGE_*` | Firebase config |
| `BACKBLAZE_S3_*` | Backblaze S3 config |
| `PROXY_CDN_URL` | CDN proxy URL |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Network                           │
│                   (netfliz-network)                         │
│                                                             │
│  ┌─────────────────┐  ┌──────────────┐  ┌───────────────┐  │
│  │  netfliz-api    │  │   postgres   │  │     redis     │  │
│  │  (Spring Boot)  │  │  (PostgreSQL)│  │   (Cache)     │  │
│  │     :8080       │──│    :5432     │  │    :6379      │  │
│  └────────┬────────┘  └──────┬───────┘  └───────┬───────┘  │
│           │                  │                  │          │
└───────────┼──────────────────┼──────────────────┼──────────┘
            │                  │                  │
    ┌───────┴───────┐  ┌───────┴───────┐  ┌───────┴───────┐
    │   Host:8080   │  │  Host:5432    │  │   Host:6379   │
    └───────────────┘  └───────────────┘  └───────────────┘

Volumes:
├── postgres_data  → /var/lib/postgresql/data
└── redis_data     → /data
```

---

## 📖 Commands Reference

### Service Management

```bash
# Start all services
docker compose up -d

# Start with build
docker compose up -d --build

# Stop all services
docker compose down

# Stop and remove volumes (⚠️ DATA LOSS)
docker compose down -v

# Restart specific service
docker compose restart netfliz-api

# View status
docker compose ps
```

### Logs

```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f netfliz-api
docker compose logs -f postgres

# Last 100 lines
docker compose logs --tail 100 netfliz-api
```

### Scaling (Optional)

```bash
# Scale API (behind load balancer)
docker compose up -d --scale netfliz-api=3
```

### Database Access

```bash
# Connect to PostgreSQL
docker compose exec postgres psql -U netfliz -d netfliz

# Backup database
docker compose exec postgres pg_dump -U netfliz netfliz > backup.sql

# Restore database
cat backup.sql | docker compose exec -T postgres psql -U netfliz -d netfliz
```

### Redis Access

```bash
# Connect to Redis CLI
docker compose exec redis redis-cli -a your_password

# Monitor Redis
docker compose exec redis redis-cli -a your_password MONITOR
```

---

## 🏭 Production Considerations

### Security Checklist

- [ ] Change all default passwords
- [ ] Use strong JWT secret key (256-bit)
- [ ] Configure proper CORS origins
- [ ] Use HTTPS with reverse proxy (nginx/traefik)
- [ ] Limit exposed ports (remove postgres/redis ports in prod)

### Performance Tuning

```yaml
# docker-compose.override.yml (production)
services:
  netfliz-api:
    deploy:
      resources:
        limits:
          memory: 2G
        reservations:
          memory: 1G
    environment:
      - JAVA_OPTS=-XX:MaxRAMPercentage=75.0 -XX:+UseG1GC
```

### Reverse Proxy (nginx example)

```nginx
server {
    listen 80;
    server_name api.yourdomain.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name api.yourdomain.com;
    
    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;
    
    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

---

## 🔧 Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| Container exits immediately | `docker compose logs netfliz-api` - check for startup errors |
| Database connection refused | Ensure postgres is healthy: `docker compose ps` |
| Port already in use | Change port in `.env` or stop conflicting service |
| Out of memory | Increase Docker memory limit or reduce `MaxRAMPercentage` |

### Health Check

```bash
# Check API
curl http://localhost:8080/api-docs

# Check Swagger UI
curl http://localhost:8080/swagger-ui.html
```

### Complete Reset

```bash
# Stop everything
docker compose down

# Remove all data (⚠️ CAUTION)
docker compose down -v

# Remove images
docker compose down --rmi all

# Clean rebuild
docker compose up -d --build --force-recreate
```

---

## 📍 Endpoints

| Endpoint | Description |
|----------|-------------|
| `http://localhost:8080` | API Base |
| `http://localhost:8080/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/api-docs` | OpenAPI Spec |

---

## 📞 Support

For issues related to deployment, check the logs first:
```bash
docker compose logs -f --tail 200
```
