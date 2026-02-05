output "instance_id" {
  description = "EC2 instance ID"
  value       = aws_instance.app.id
}

output "public_ip" {
  description = "Public IP address"
  value       = aws_eip.app.public_ip
}

output "app_url" {
  description = "Application URL"
  value       = "http://${aws_eip.app.public_ip}:8080"
}

output "app_domain" {
  description = "Application Domain URL (HTTP)"
  value       = "http://${var.subdomain}.${var.domain_name}"
}

output "app_domain_https" {
  description = "Application Domain URL (HTTPS)"
  value       = "https://${var.subdomain}.${var.domain_name}"
}

output "ssh_command" {
  description = "SSH command to connect"
  value       = "ssh -i ~/.ssh/${var.key_name}.pem ec2-user@${aws_eip.app.public_ip}"
}
