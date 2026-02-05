# EC2 Instance
resource "aws_instance" "app" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = var.instance_type
  key_name               = var.key_name
  vpc_security_group_ids = [aws_security_group.app.id]

  root_block_device {
    volume_size = 30
    volume_type = "gp3"
  }

  user_data = base64encode(templatefile("${path.module}/user-data.sh", {
    slack_bot_token      = var.slack_bot_token
    slack_signing_secret = var.slack_signing_secret
    default_video_url    = var.default_video_url
    youtube_api_key      = var.youtube_api_key
    domain_name          = "${var.subdomain}.${var.domain_name}"
  }))

  tags = {
    Name = var.app_name
  }
}

# Elastic IP
resource "aws_eip" "app" {
  instance = aws_instance.app.id
  domain   = "vpc"

  tags = {
    Name = "${var.app_name}-eip"
  }
}
