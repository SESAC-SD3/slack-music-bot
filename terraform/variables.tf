variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "ap-northeast-2"
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.micro"
}

variable "key_name" {
  description = "EC2 key pair name"
  type        = string
}

variable "app_name" {
  description = "Application name"
  type        = string
  default     = "music-bot"
}

variable "slack_bot_token" {
  description = "Slack bot token"
  type        = string
  sensitive   = true
}

variable "slack_signing_secret" {
  description = "Slack signing secret"
  type        = string
  sensitive   = true
}

variable "default_video_url" {
  description = "Default YouTube video URL"
  type        = string
  default     = "https://www.youtube.com/watch?v=jfKfPfyJRdk"
}

# Domain
variable "domain_name" {
  description = "Root domain name"
  type        = string
}

variable "subdomain" {
  description = "Subdomain for the application"
  type        = string
  default     = "music"
}
