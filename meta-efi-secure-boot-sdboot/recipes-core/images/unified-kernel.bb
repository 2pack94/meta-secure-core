LICENSE = "MIT"

inherit uefi-comboapp

#WKS_FILE = "generic-bootdisk.wks.in"
#DEPENDS_remove = "grub-efi"

IMAGE_FEATURES += "secureboot"

#TEST
SECURE_BOOT_SIGNING_KEY ?= "/home/richard/redkeep-os_yocto/redkeep-os/sources/meta-secure-core/meta-signing-key/scripts/user-keys/uefi_sb_keys/DB.key"
SECURE_BOOT_SIGNING_CERT ?= "/home/richard/redkeep-os_yocto/redkeep-os/sources/meta-secure-core/meta-signing-key/scripts/user-keys/uefi_sb_keys/DB.crt"

# need to be set explicitly, otherwise it will get overridden inside uefi-comboapp.bbclass
INITRD_IMAGE="${INITRAMFS_IMAGE}"

# kernel command line for the unified kernel image
APPEND += "root=/dev/sda2 rw rootwait"
