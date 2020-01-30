FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
  file://0001-fix-wic-installer-copy-all-bootloader-files.patch \
  ${@bb.utils.contains("DISTRO_FEATURES", "luks", "file://0002-wic-installer-add-full-disk-encryption-support.patch", "", d)} \
"

# add packages to the wic image installer initramfs for full disk encryption
RDEPENDS_${PN} += " \
  ${@bb.utils.contains("DISTRO_FEATURES", "luks", "cryptfs-tpm2", "", d)} \
  ${@bb.utils.contains("DISTRO_FEATURES", "luks", "cryptsetup", "", d)} \
"
