DESCRIPTION = "UEFI Secure Boot packages for systemd-boot."
LICENSE = "MIT"

# The recipe doesn't install anything.
ALLOW_EMPTY_${PN} = "1"

DEPENDS = " \
    efitools \
"
