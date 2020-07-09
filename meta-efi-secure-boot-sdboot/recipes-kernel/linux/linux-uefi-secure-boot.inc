# Sign the kernel for UEFI Secure Boot
# A signed kernel is only needed when its used as standalone and it's not unified
# Make sure the kernel image has been signed before kernel_do_deploy() which prepares it for creating usb/iso.

# the signing key locations are defined in layer.conf
SIGN_AFTER = "do_install"
SIGN_BEFORE = "do_package do_populate_sysroot do_deploy"
SIGNING_DIR = "${B}/${KERNEL_OUTPUT_DIR}"
SIGNING_BINARIES = "${KERNEL_IMAGETYPES}"
inherit uefi-sign

# remove any previous signatures, if necessary
do_uefi_sign_prepend() {
    if [ -f ${SECURE_BOOT_SIGNING_KEY} ] && [ -f ${SECURE_BOOT_SIGNING_CERT} ]; then
        for i in `find ${SIGNING_DIR}/ -name '${SIGNING_BINARIES}'`; do
            sbattach --remove $i
        done
    fi
}
