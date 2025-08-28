# SimpleAuthPlugin

A simple authentication plugin for Minecraft servers, based on AuthMeReloaded but with plain text password storage.

## Features

- **Authentication System**
  - Login/register commands
  - Session management
  - Plain text password storage
  - Premium (paid Minecraft) account support

- **Database Support**
  - MySQL
  - SQLite
  - YAML

- **Player Protection**
  - Movement restriction
  - Chat restriction
  - Inventory protection
  - Command restriction
  - Damage protection

- **Configuration Options**
  - Customizable messages
  - Session timeout
  - Registration settings
  - Restriction settings

## Commands

- `/register <password> [email]` - Register an account
- `/login <password>` - Login to the server
- `/changepassword <oldPassword> <newPassword>` - Change your password
- `/logout` - Logout from the server
- `/unregister <password>` - Unregister your account
- `/authreload` - Reload the plugin configuration (admin)
- `/authstatus` - Check the authentication status (admin)

## Installation

1. Download the latest release from the releases page
2. Place the JAR file in your server's `plugins` folder
3. Restart the server
4. Edit the `config.yml` file to customize the plugin

## Configuration

The plugin is highly configurable. You can customize:

- Database type (MySQL, SQLite, YAML)
- Registration settings
- Login settings
- Session settings
- Spawn settings
- Restriction settings
- Messages

## Security Note

This plugin stores passwords in plain text, which is not recommended for production use. This implementation is provided for educational purposes or for servers where security is not a concern.

## Building

To build the plugin:

```bash
mvn clean package
```

The compiled JAR file will be in the `target` directory.

## Requirements

- Java 21 or higher
- Minecraft 1.21.x
- Paper/Spigot server

## License

This project is licensed under the MIT License - see the LICENSE file for details.

