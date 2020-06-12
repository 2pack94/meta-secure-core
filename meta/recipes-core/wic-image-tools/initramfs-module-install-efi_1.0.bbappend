FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
SRC_URI += " \
  file://0001-init-install-efi-support-secure-boot-files-and-full-disk-encryption.patch \
"

python () {
    if d.getVar("FULL_DISK_ENCRYPTION") == "1" and bb.utils.contains("DISTRO_FEATURES", "luks", True, False, d):
        # add packages to the wic image installer initramfs for full disk encryption
        d.appendVar(d.expand("RDEPENDS_${PN}"), " \
          cryptfs-tpm2 \
          cryptsetup \
          " \
        )
}

do_install_append() {
    if [ "${FULL_DISK_ENCRYPTION}" = "1" ] && [ ${@bb.utils.contains("DISTRO_FEATURES", "luks", 'true', '', d)} ]; then
        sed -i '0,/do_encryption=0/s//do_encryption=1/' ${D}/init.d/install-efi.sh
    fi
    if [ "${INITRAMFS_IMAGE}" ]; then
        sed -i '0,/use_initramfs=0/s//use_initramfs=1/' ${D}/init.d/install-efi.sh
    fi
}
