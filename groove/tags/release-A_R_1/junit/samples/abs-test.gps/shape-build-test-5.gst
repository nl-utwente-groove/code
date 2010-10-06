<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="shape-build-test-5">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n2"/>
        <node id="n1"/>
        <node id="n0"/>
        <node id="n3"/>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>flag:ld</string>
            </attr>
        </edge>
        <edge to="n0" from="n2">
            <attr name="label">
                <string>ldr</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>flag:flw</string>
            </attr>
        </edge>
        <edge to="n0" from="n3">
            <attr name="label">
                <string>ldr</string>
            </attr>
        </edge>
        <edge to="n0" from="n1">
            <attr name="label">
                <string>ldr</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>flag:flw</string>
            </attr>
        </edge>
        <edge to="n3" from="n0">
            <attr name="label">
                <string>flws</string>
            </attr>
        </edge>
        <edge to="n1" from="n0">
            <attr name="label">
                <string>flws</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>flag:flw</string>
            </attr>
        </edge>
        <edge to="n2" from="n0">
            <attr name="label">
                <string>flws</string>
            </attr>
        </edge>
    </graph>
</gxl>
