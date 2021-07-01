DESCRIPTION = "UEFI Secure Boot packages for systemd-boot."
LICENSE = "MIT"

S = "${WORKDIR}"

# The recipe doesn't install anything.
ALLOW_EMPTY_${PN} = "1"

pkgs = "\
    efitools \
"
RDEPENDS_${PN}_x86 = "${pkgs}"
RDEPENDS_${PN}_x86-64 = "${pkgs}"

kmods = "\
    kernel-module-efivarfs \
    kernel-module-efivars \
"
RRECOMMENDS_${PN}_x86 += "${kmods}"
RRECOMMENDS_${PN}_x86-64 += "${kmods}"
