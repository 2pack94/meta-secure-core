FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
  file://0001-fix-wic-installer-copy-all-bootloader-files.patch \
"

python () {
    if d.getVar("FULL_DISK_ENCRYPTION") == "1" and bb.utils.contains("DISTRO_FEATURES", "luks", True, False, d):      
        d.appendVar("SRC_URI", "file://0002-wic-installer-add-full-disk-encryption-support.patch")
        # add packages to the wic image installer initramfs for full disk encryption
        d.appendVar(d.expand("RDEPENDS_${PN}"), " \
          cryptfs-tpm2 \
          cryptsetup \
          " \
        )
}
