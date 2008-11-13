<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="start" role="graph" edgeids="false" edgemode="directed">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0"/>
        <node id="n8"/>
        <node id="n1"/>
        <node id="n4"/>
        <node id="n3"/>
        <node id="n6"/>
        <node id="n9"/>
        <node id="n2"/>
        <node id="n7"/>
        <node id="n5"/>
        <node id="n10"/>
        <edge from="n9" to="n9">
            <attr name="label">
                <string>Scheduler</string>
            </attr>
        </edge>
        <edge from="n6" to="n4">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n3" to="n1">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n6" to="n2">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n8" to="n8">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n9" to="n8">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n8" to="n3">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>int:1</string>
            </attr>
        </edge>
        <edge from="n10" to="n10">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n1" to="n10">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n9" to="n1">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n8" to="n5">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n10" to="n8">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>int:-1</string>
            </attr>
        </edge>
        <edge from="n3" to="n5">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n9" to="n3">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n6" to="n0">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>int:2</string>
            </attr>
        </edge>
        <edge from="n8" to="n8">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n6" to="n7">
            <attr name="label">
                <string>number</string>
            </attr>
        </edge>
        <edge from="n7" to="n7">
            <attr name="label">
                <string>int:4</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>active</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>int:3</string>
            </attr>
        </edge>
        <edge from="n10" to="n5">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n1" to="n5">
            <attr name="label">
                <string>left</string>
            </attr>
        </edge>
        <edge from="n6" to="n6">
            <attr name="label">
                <string>Numbers</string>
            </attr>
        </edge>
        <edge from="n10" to="n10">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
        <edge from="n9" to="n10">
            <attr name="label">
                <string>init</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>Process</string>
            </attr>
        </edge>
    </graph>
</gxl>
