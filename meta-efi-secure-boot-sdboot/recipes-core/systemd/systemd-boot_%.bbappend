# deploy the EFI stub loader from systemd
# copied from meta-intel/recipes-bsp/systemd-boot/systemd-boot_%.bbappend
do_compile_append() {
        ninja src/boot/efi/linux${SYSTEMD_BOOT_EFI_ARCH}.efi.stub
}

do_deploy_append() {
        install ${B}/src/boot/efi/linux*.efi.stub ${DEPLOYDIR}
}

# Sign the systemd bootloader
# The signing key locations are specified in the unified-kernel recipe in this layer
SIGN_AFTER = "do_deploy"
SIGN_BEFORE = "do_build"
SIGNING_DIR = "${DEPLOY_DIR_IMAGE}"
SIGNING_BINARIES = "systemd-boot*.efi"
inherit uefi-sign
