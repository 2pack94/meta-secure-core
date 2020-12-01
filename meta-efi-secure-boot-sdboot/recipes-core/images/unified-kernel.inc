# This recipe inherits from uefi-comboapp.bbclass in meta-intel to create an unified kernel image that is signed for UEFI Secure Boot
# It can be loaded and verified by systemd-boot
# A recipe that inherits uefi-comboapp needs to have the tasks do_rootfs and do_image in this order

# the signing key locations are defined in layer.conf
do_uefi_sign[vardeps] += "UEFI_SB_KEYS_DIR"
inherit uefi-comboapp

# need to be set explicitly, otherwise it will get overridden inside uefi-comboapp.bbclass
INITRD_IMAGE = "${INITRAMFS_IMAGE}"
# need to be set explicitly, otherwise it will not get set inside uefi-comboapp.bbclass
INITRD_LIVE = " ${@ ('${DEPLOY_DIR_IMAGE}/' + d.getVar('INITRD_IMAGE', expand=True) + '-${MACHINE}.cpio.gz') if d.getVar('INITRD_IMAGE', True) else ''}"

# empty this variable, as the rootfs partition is loaded according to its disk name.
DISK_SIGNATURE_UUID ?= ""

# kernel command line for the unified kernel image. Overrides settings from other layers.
APPEND = "root=/dev/sda2 rw rootwait quiet"

# create a os-release file and add it as a section to the unified kernel image.
# This is needed by systemd-boot to display the image in the boot menu.
python do_uefiapp_append() {
    import glob, re
    from subprocess import check_call

    app_suffix = ''
    build_dir = d.getVar('B')
    deploy_dir_image = d.getVar('DEPLOY_DIR_IMAGE')
    image_link_name = d.getVar('IMAGE_LINK_NAME')

    stub_path = '%s/linux*.efi.stub' % deploy_dir_image
    stub = glob.glob(stub_path)[0]
    m = re.match(r"\S*(ia32|x64)(.efi)\S*", os.path.basename(stub))
    app = "boot%s%s%s" % (m.group(1), app_suffix, m.group(2))
    executable = '%s/%s.%s' % (deploy_dir_image, image_link_name, app)

    os_release = d.getVar('OS_RELEASE_FILE')
    # gernerate default os-release file if none was specified
    if not os_release:
        os_release = '%s/os-release.txt' % build_dir

        os_release_content = (
            'NAME="deafult-OS"\n'
            'VERSION="1"\n'
            'ID=default\n'
            'PRETTY_NAME="Linux"\n'
            'VERSION_ID="1"\n'
        )

        with open(os_release, 'w') as f:
            f.write('%s' % os_release_content)

    objcopy_cmd = ("objcopy "
        "--add-section .osrel=%s --change-section-vma .osrel=0x20000 "
        "%s %s") % \
        (os_release, executable, executable)

    check_call(objcopy_cmd, shell=True)
}
