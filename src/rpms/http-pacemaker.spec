%define jboss_version 6.1.0

Name:	cobbler-proxy
Version:	1.0
Release:	1%{?dist}
Summary:	A simple HTTP Proxy designed to "slow down" incoming request to prevent
            overload of the backend.

Group:      Administration
License:	LGPL
URL:        http://access.redhat.com/

Packager:   Romain Pelisse
BuildArch:  noarch

Requires(pre): shadow-utils

%define username jboss
%define group jboss
%define jboss_home /opt/jboss

%pre
getent group %{group} > /dev/null || groupadd -r %{group}
getent passwd %{username}  > /dev/null || \
    useradd -r -g %{group} -d %{jboss_home} -s /sbin/nologin \
        -c "JBoss EAP 6 user account" %{username}
ln -s %{jboss_home}/bin/init.d/jboss-as-standalone.sh /etc/init.d/%{name}
exit 0

%clean
exit 0

%description

%post
service %{name} start

%files
%defattr(-,%{username},%{group})
%{jboss_home}
/etc/jboss-as/jboss-as.conf
%defattr(-,root,root)
#/etc/init.d/%{name}

%doc


%changelog

