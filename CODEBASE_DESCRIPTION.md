# MoneyMoneyMoney Codebase Description

## Overview

`MoneyMoneyMoney` is a Minecraft Forge mod for Minecraft `1.20.1` built with Java 17. It adds a lightweight player economy centered around a personal wallet, a bank balance, a consumable money item, chat commands, and a simple client menu for managing balances.

From the code currently in the repository, the mod is designed as a self-contained money system rather than a full shop or trading framework. The main gameplay loop is:

1. Players obtain `Money` items.
2. Using a `Money` item converts it into wallet currency.
3. Wallet currency can be deposited into a bank balance or withdrawn back out.
4. Players can transfer wallet currency to other online players.
5. Operators can mint additional money items with a command.

## Player-Facing Features

### 1. Money item

The mod registers a single custom item:

- `money`

When a player uses the item:

- it adds `1` to the player's wallet balance
- it consumes one item unless the player is in an instabuild/creative-like mode
- it shows a status message with the updated wallet total

The item is stackable up to 64.

### 2. Wallet and bank balances

Each player has two tracked balances:

- `walletBalance`
- `bankBalance`

These balances are stored in a Forge capability attached to the player entity. The capability:

- persists as NBT data
- prevents balances from dropping below zero
- supports add/remove operations for both wallet and bank
- provides convenience methods for deposit and withdrawal

### 3. Commands

The `/money` command tree provides several actions for server players:

- `/money balance`
  Shows wallet and bank totals.
- `/money menu`
  Opens the money management screen.
- `/money deposit <amount>`
  Moves money from wallet to bank.
- `/money withdraw <amount>`
  Moves money from bank to wallet.
- `/money transfer <player> <amount>`
  Sends wallet money to another online player.
- `/money mint <amount>`
  Operator-only command that creates `Money` items and places them into inventory.

Transfer logic includes protection against:

- invalid target players
- self-transfer
- spending more wallet money than the sender has

### 4. Money management screen

The mod includes a custom menu and client screen called `Money Manager`.

The screen displays:

- wallet balance
- bank balance
- transfer inputs for target player and amount
- deposit amount input
- withdraw amount input

The UI sends actions to the server through a custom packet, and the server validates:

- that the packet came from a player currently using the money menu
- that the amount is greater than zero

## Technical Structure

### Mod bootstrap

The main mod entrypoint registers:

- item registry
- menu registry
- network messages during common setup

### Registries

The project currently registers:

- one item: `money`
- one menu type: `money_menu`

### Capability system

The economy state is implemented with:

- `IPlayerMoneyData` interface
- `PlayerMoneyCapability` concrete implementation
- `PlayerMoneyProvider` capability provider
- `ModCapabilities` capability lookup holder
- `PlayerMoneyCapabilityRegistration` Forge capability registration
- `PlayerMoneyEvents` capability attachment and clone-copy handling

When players are cloned, wallet and bank balances are copied from the old player instance to the new one.

### Networking

The networking layer uses a `SimpleChannel` with a single packet:

- `MoneyActionPacket`

Supported packet actions:

- `TRANSFER`
- `DEPOSIT`
- `WITHDRAW`

This keeps server authority over all balance changes initiated from the UI.

### Client integration

Client-only setup registers the screen for the custom menu type so the menu opened on the server has a matching client GUI.

## Assets and Resources

The resource folder contains:

- English localization entries for commands, buttons, labels, and system messages
- item models
- item textures for several bill denominations:
  - `one_dollar`
  - `five_dollars`
  - `ten_dollars`
  - `twenty_dollars`
  - `fifty_dollars`

From the current Java registry code, only the generic `money` item is actually registered and usable. The extra denomination textures and models appear to be preparatory assets for future expansion.

## Observations About Current State

The codebase is functional as a basic personal-currency mod, but it also shows signs of being in-progress:

- `gradle.properties` still contains Forge example template metadata such as `mod_id=examplemod` and `mod_name=Example Mod`
- the Java package names and runtime mod ID are already customized to `moneymoneymoney`
- resource assets include multiple bill denominations that are not yet registered in code
- `mods.toml` and `pack.mcmeta` still rely on template substitutions, so packaging metadata likely needs cleanup before release

## Summary

In its current form, `MoneyMoneyMoney` is a Forge mod that implements a simple per-player economy with:

- a redeemable money item
- wallet and bank storage
- player-to-player transfers
- a management GUI
- command-based administration

It is a solid foundation for a larger economy mod, especially if future work adds multiple bill types, shops, merchants, or persistence beyond the player capability system.

### Note: Generated by OpenAI's Codex in Visual Studio Code