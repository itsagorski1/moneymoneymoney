# Platform builds

The repository root remains the original Forge 1.20.1 project. Additional standalone builds live here so incompatible loader and Minecraft APIs do not overwrite each other.

## Targets

| Directory | Minecraft | Loader | Java | Build command |
| --- | --- | --- | --- | --- |
| `neoforge-1.20.1` | 1.20.1 | NeoForge 47.1.106 | 17 | `./gradlew jar` |
| `neoforge-26.3` | 26.3 | NeoForge 26.3.0.69-beta | 25 | `./gradlew jar` |
| `forge-26.3` | 26.3 | Forge 66.0.10 | 25 | `./gradlew jar` |

Run each command from its target directory. Each target has the official Gradle wrapper version required by its loader.

The 1.20.1 NeoForge build preserves the complete 1.20.1 implementation. The 26.3 builds share the same core port: five redeemable bills, persistent wallet and bank balances, transfers, deposits, withdrawals, operator minting, the `/walletbalance` compatibility command, and the five bank blocks. `/money menu` reports the balances on 26.3 while the graphical menu and Banker entity await a later 26.3 client/network port.

Generated JARs are written to each target's `build/libs` directory and include both the loader and Minecraft version in the filename.
