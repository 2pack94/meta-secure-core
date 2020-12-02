SUMMARY = "Basic init for initramfs to mount and pivot root"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
    file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302 \
"

SRC_URI = "\
    file://init \
"

do_install() {
    install -m 0755 "${WORKDIR}/init" "${D}/init"

    if [ "${FULL_DISK_ENCRYPTION}" = "1" ] && [ ${@bb.utils.contains("DISTRO_FEATURES", "luks", 'true', '', d)} ]; then
        sed -i '0,/is_encrypted=0/s//is_encrypted=1/' ${D}/init
    fi
    if [ "${IS_PRODUCTION}" = "1" ]; then
        sed -i '0,/LOG_LEVEL=1/s//LOG_LEVEL=0/' ${D}/init
    fi

    # Create device nodes expected by kernel in initramfs
    # before executing /init.
    install -d "${D}/dev"
    install -d "${D}/run"
    mknod -m 0600 "${D}/dev/console" c 5 1
}

FILES_${PN} = "\
    /init \
    /dev \
    /run \
"

# Install the minimal stuffs only, and don't care how the external
# environment is configured.

# @busybox: echo, cat, sleep, switch_root, expr, mkdir, mount, grep, awk
# @eudev or udev: udevd, udevadm
RDEPENDS_${PN} += "\
    busybox \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'udev', 'eudev', d)} \
"

# @initrdscripts-ima: init.ima
# @cryptfs-tpm2-initramfs: init.cryptfs
RRECOMMENDS_${PN} += "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'ima', 'initrdscripts-ima', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'luks', 'cryptfs-tpm2-initramfs', '', d)} \
"
